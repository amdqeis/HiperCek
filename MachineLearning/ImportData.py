import kagglehub

# Download latest version
path = kagglehub.dataset_download("ankushpanday1/hypertension-risk-prediction-dataset")

print("Path to dataset files:", path)