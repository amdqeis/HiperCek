const port = process.env.PORT || "8000";
const appName = process.env.PM2_APP_NAME || "hipercek-ml-api";

module.exports = {
  apps: [
    {
      name: appName,
      cwd: __dirname,
      script: "./venv/bin/uvicorn",
      args: `api:app --host 127.0.0.1 --port ${port}`,
      interpreter: "none",
      env: {
        PORT: port,
        PYTHONUNBUFFERED: "1",
      },
    },
  ],
};
