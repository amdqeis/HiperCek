import kagglehub

# Download latest version
path = kagglehub.dataset_download("ankushpanday1/hypertension-risk-prediction-dataset")

print("Path to dataset files:", path)

from kagglehub import KaggleDatasetAdapter

path = kagglehub.dataset_download("sulianova/cardiovascular-disease-dataset")

# Load the latest version
# df = kagglehub.load_dataset(
#   KaggleDatasetAdapter.PANDAS,
#   "sulianova/cardiovascular-disease-dataset",
#   file_path,
# )

# print("First 5 records:", df.head())
print("Path to dataset files:", path)
