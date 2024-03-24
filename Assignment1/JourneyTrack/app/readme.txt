Welcome to JourneyTrack, a simple Android application that helps you track your journey stops and distances. This README document will guide you through the key aspects of the working of the application, with a focus on the MainActivity.kt and its associated XML layout file (activity_main.xml).

- Overview -
JourneyTrack is designed to display a list of stops along a journey, allowing you to track the total distance covered and remaining distance. The main screen (MainActivity) provides an interactive interface to view and navigate through the stops.

- App Structure -
The application consists of the following components:
1. Splash Activity (SplashActivity.kt and activity_splash.xml):
	The splash screen is a visual introduction to the app displayed when the application is launched.
	SplashActivity.kt initiates the splash screen with a GIF image and transitions to MainActivity after a brief delay.
2. Main Activity (MainActivity.kt and activity_main.xml):
	The central part of the application that displays the list of stops, progress bar, and other UI elements.
3. Data Model (Stop class):
	The Stop data class represents a single stop with a name and distance in kilometers. This class is utilized to 	structure the stops for the journey.

- Understanding MainActivity.kt -
-- Members and Properties --
Buttons and Switches:
1. btnNextStop: Button to navigate to the next stop.
2. switchUnits: Switch for toggling between displaying distances in kilometers or miles.
3. switchLists: Switch for toggling between normal and lazy stops lists.

TextViews:
tvTotalDistanceCovered and tvTotalDistanceRemaining: Display the total distance covered and remaining in the journey.

Lists of Stops:
normalStops and lazyStops: Lists containing stops with their respective distances.

State Variables:
1. currentStop: Tracks the index of the current stop.
2. useLazyStops: Tracks whether to use normal or lazy stops.
3. progressState: Maintains the progress state for the progress bar.

-- Functions --
onCreate:
Initializes the UI elements and sets up listeners for switches and buttons.
Displays the initial set of stops based on the default configuration.

displayStops:
Updates the UI to display stops based on the current configuration.

StopsList and LazyStopsList Composables:
Composable functions to display lists of stops using Jetpack Compose.

StopCard Composable:
Composable function to display a card for each stop in the list.

Distance Calculation Functions:
calculateTotalDistanceCovered and calculateTotalDistanceRemaining: Functions to compute the total distance covered and remaining based on the selected stops.

ProgressBar Functions:
ProgressBar and updateProgressBar: Functions to display and update the progress bar.

Toast Function:
showToast: Displays toast messages for specific events.

-- Compose UI Components --
AppLogo Composable - Displays the application logo using Jetpack Compose.
ProgressBar Composable - Creates a linear progress bar using Jetpack Compose.

-- Integration with XML Layout --
ComposeView Elements:
logo_view, progress_view, compose_view: Integration of Jetpack Compose elements within the XML layout.

TextViews:
tv_total_distance_covered and tv_total_distance_remaining: Display total distances and are dynamically updated in the StopsList and LazyStopsList composables.

-- Usage of Third-party Libraries --
Glide Library - Utilized to load and display GIF images efficiently.

- App Usage -
1. The app starts with a splash screen displaying the application logo.
2. After the splash screen, you are directed to the main screen where you can:
	- Toggle between kilometers and miles.
	- Toggle between normal and lazy stops.
	- Navigate to the next stop using the "Next Stop" button.
	- View the total distance covered and remaining.
3. Stop Lists:
	- The main screen displays either normal or lazy stops based on the selected configuration.
4. Progress Bar:
	- The progress bar at the top updates every time the user navigates to the next stop.