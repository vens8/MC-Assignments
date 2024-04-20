import pandas as pd
from statsmodels.tsa.arima.model import ARIMA
import matplotlib.pyplot as plt

# Read the data from the text file
data = pd.read_csv('orientation_data.txt', sep=',', header=None, names=['x', 'y', 'z', 'timestamp'], engine='python')
print(data)

# Convert the timestamp to datetime format (assuming the timestamp is in milliseconds)
data['timestamp'] = pd.to_datetime(data['timestamp'], unit='ms')

# Set the index to the datetime column and explicitly set the frequency to 'S' (seconds)
data.set_index('timestamp', inplace=True)
data.index = data.index.to_period('s')

# Fit an ARIMA model to the 'x', 'y', and 'z' values
model_x = ARIMA(data['x'], order=(5,1,0)).fit()
model_y = ARIMA(data['y'], order=(5,1,0)).fit()
model_z = ARIMA(data['z'], order=(5,1,0)).fit()

# Make predictions for the next 10 seconds
# Here, we're creating a date range starting from the last timestamp in the data.
future_steps = pd.date_range(start=data.index[-1].to_timestamp(), periods=11, freq='s')

predictions_x = model_x.forecast(len(future_steps))
predictions_y = model_y.forecast(len(future_steps))
predictions_z = model_z.forecast(len(future_steps))

print("data date index")
print(data.index.to_timestamp())

print("future steps")
print(future_steps)


print(data['x'])
print(predictions_x)
print()
print(data['y'])
print(predictions_y)
print()
print(data['z'])
print(predictions_z)

# Plot the actual vs predicted values with detailed labels
plt.figure(figsize=(12, 8))

# Adding a main title for the figure
plt.suptitle('Orientation Data Prediction', fontsize=16)

plt.subplot(3, 1, 1)
plt.plot(data.index.to_timestamp(), data['x'], label='Actual X')
plt.plot(future_steps, predictions_x, label='Predicted X', linestyle='--', color='red')
plt.legend()
plt.title('X Axis Orientation')
plt.xlabel('Timestamp')  # Adding an x-axis label
plt.ylabel('X Orientation')  # Adding a y-axis label

plt.subplot(3, 1, 2)
plt.plot(data.index.to_timestamp(), data['y'], label='Actual Y')
plt.plot(future_steps, predictions_y, label='Predicted Y', linestyle='--', color='red')
plt.legend()
plt.title('Y Axis Orientation')
plt.xlabel('Timestamp')  # Adding an x-axis label
plt.ylabel('Y Orientation')  # Adding a y-axis label

plt.subplot(3, 1, 3)
plt.plot(data.index.to_timestamp(), data['z'], label='Actual Z')
plt.plot(future_steps, predictions_z, label='Predicted Z', linestyle='--', color='red')
plt.legend()
plt.title('Z Axis Orientation')
plt.xlabel('Timestamp')  # Adding an x-axis label
plt.ylabel('Z Orientation')  # Adding a y-axis label

plt.tight_layout(rect=[0, 0.03, 1, 0.95])  # Adjust layout to make room for the main title
plt.show()