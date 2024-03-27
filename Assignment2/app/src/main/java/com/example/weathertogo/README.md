# WeatherToGo: A Multi-Approach Weather App
This Android application demonstrates two approaches to retrieving weather data based on user-selected date, latitude, and longitude:

## Direct Network API Call (Q1Screen)
 This approach fetches weather information directly from an external weather API using Retrofit.
## Room Persistence with Network Fallback (Q2Screen):
Apart from fetching the weather information for some coordinates and date directly from the open-mateo API, it also stores the results into the database. When there's no internet connectivity, it looks up the database to check for any previously fetched/stored results and uses them to display to the user. 

## Project Structure:    
    ├── MainActivity.kt
    ├── ui/
    │   ├── components/
    │   │   └── OutlinedRow.kt
    │   ├── theme/
    │   │   ├── Color.kt
    │   │   ├── Theme.kt
    │   │   └── Type.kt
    │   ├── utility/
    │   │   └── HelperFunctions.kt
    │   ├── LandingScreen.kt
    │   ├── Q1Screen.kt
    │   └── Q2Screen.kt
    ├── viewmodel/
    │   ├── WeatherViewModelFactoryQ1.kt
    │   ├── WeatherViewModelFactoryQ2.kt
    │   ├── WeatherViewModelQ1.kt
    │   └── WeatherViewModelQ2.kt
    └── db/
        ├── utlity/
        │   └── Convertors.kt
        ├── WeatherDao.kt
        ├── WeatherDatabase.kt
        └── WeatherDataEntity.kt


app: Contains the main application code, including activities, view models, UI components, and resources.
activities: Houses the various screens of the application (e.g., LandingScreen, Q1Screen, Q2Screen).
viewmodels: Contains the WeatherViewModelQ1 and WeatherViewModelQ2 classes responsible for weather data retrieval and management.
ui: May contain custom UI components for reusability.
res: Stores application resources like layouts, drawables, and strings.
data: May contain data classes or models specific to the weather data.


## Room Persistence (Database):
The application utilizes a Room database for local storage of weather data. Here's a breakdown of the relevant files:

WeatherDatabase.kt: Defines the Room database schema with a single entity, WeatherDataEntity.
WeatherDao.kt: Provides data access methods for the WeatherDataEntity, including insertWeatherData for storing and getWeatherDataByDateAndCoordinates for retrieval.
View Models (Q1 vs. Q2):

Both WeatherViewModelQ1 and WeatherViewModelQ2 inherit from ViewModel and share some functionalities:

Weather Data: Expose observable weather data (weatherInfo) and messages (messages) using LiveData for UI updates.
Selected Date & Location: Allow setting and retrieving the user-selected date (selectedDate) and latitude/longitude (selectedLatitude, selectedLongitude).
Error Handling: Can display informative messages through the messages observable.


### Entity
```
@Entity(tableName = "weather_data", primaryKeys = ["date", "latitude", "longitude"])
data class WeatherDataEntity(
    val date: LocalDate,
    val latitude: Double,
    val longitude: Double,
    val tempMax: Double,
    val tempMin: Double,
    val location: String,
    val isAverage: Boolean,
)
```
### DAO
```
@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(weatherData: WeatherDataEntity)

    @Query("SELECT * FROM weather_data WHERE date = :date AND latitude = :latitude AND longitude = :longitude")
    suspend fun getWeatherDataByDateAndCoordinates(latitude: Double, longitude: Double, date: LocalDate): WeatherDataEntity?
}
```

### Database
```
@Database(entities = [WeatherDataEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null
        fun getInstance(context: Context): WeatherDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WeatherDatabase::class.java,
                        "weather_database"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
```

This database instance is created and initialised in ``WeatherViewModelQ2``.

## Key Differences

### Data Source:
WeatherViewModelQ1: Fetches data solely from the network API.

WeatherViewModelQ2: Checks for internet connectivity first, then fetches data from the API and stores to database. If there's no internet connectivity, it checks for availability of data for the given coordinates and date and fetches the report.

Data Retrieval Method:
WeatherViewModelQ1: Implements getWeatherData to directly call the network API using Retrofit.
WeatherViewModelQ2: Check internet, call API if available and store to database using insertWeatherData() defined in DAO. If no internet, call getWeatherData to check the Room database using getWeatherDataByDateAndCoordinates and then display to the accordingly.

### Retrofit Integration:

Retrofit is a popular HTTP client library used to simplify API interactions in Android. Here's how it's implemented in Q1Screen:

Dependency: The retrofit library is included in the app's dependencies using Gradle.
API Interface: An interface (WeatherService) is defined, outlining methods for weather data retrieval. This interface uses annotations to specify HTTP methods (e.g., @GET) and URL endpoints.
Retrofit Instance: A Retrofit instance is created with the base URL of the weather API.
API Call: Inside getWeatherData of WeatherViewModelQ1, Retrofit is used to create a service instance and call the appropriate API endpoint. The user-provided latitude, longitude, and date are formatted as query parameters in the URL.
Parsing Response: The response from the API is parsed using a converter (e.g., Gson) into a weather data object.
Weather URL Formation:
Q1Screen (Network API): The weather URL is dynamically built using string formatting, including the base URL, API key, and user-provided latitude, longitude, and date in the required format (e.g., YYYY-MM-DD).
Room Database Implementation:

Entity Class: WeatherDataEntity represents a single weather data record with fields like date, location, temperature, etc.
Database Class: WeatherDatabase extends RoomDatabase and defines the entity table.
DAO Interface: WeatherDao provides methods for interacting with the weather data table, including insertion (insertWeatherData) and retrieval (getWeatherDataByDateAndCoordinates).
Database Access: In WeatherViewModelQ2, getWeatherData first tries to retrieve data using getWeatherDataByDateAndCoordinates with the user-provided date and location.
Fallback to Network: If no data exists in the Room database, the view model falls back to fetching data from the network API using Retrofit as described earlier in the Retrofit Integration section.

## UI and State Management
The UI screens (LandingScreen, Q1Screen, Q2Screen) utilize Jetpack Compose for building the user interface. Here's an overview of state management:

MutableState: Used for user input fields like latitude, longitude, and date selection using remember composable.
DerivedState: Calculates values based on other states, such as isFormValid to enable the "Get Weather Data" button only when all input fields are valid.
LiveData Observation: Screens observe LiveData from view models (e.g., weatherInfo, messages) to update UI components dynamically when the data changes.
IconButton: Allows clearing displayed weather information by calling clearWeatherInfo on the view model.
