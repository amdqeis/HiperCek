import type { ApiError, PredictionFormData, PredictionResponse } from "./types";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL?.replace(/\/$/, "") ?? "http://localhost:8080";

async function parseError(response: Response) {
  let payload: ApiError | null = null;
  try {
    payload = (await response.json()) as ApiError;
  } catch {
    payload = null;
  }

  const error = new Error(payload?.message ?? "Terjadi kesalahan pada server.");
  if (payload?.errors) {
    Object.assign(error, { fieldErrors: payload.errors });
  }
  throw error;
}

export async function createPrediction(payload: PredictionFormData): Promise<PredictionResponse> {
  const response = await fetch(`${API_BASE_URL}/api/predictions`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    await parseError(response);
  }

  return (await response.json()) as PredictionResponse;
}

export async function getPredictionHistory(): Promise<PredictionResponse[]> {
  const response = await fetch(`${API_BASE_URL}/api/predictions/history`, {
    cache: "no-store",
  });

  if (!response.ok) {
    await parseError(response);
  }

  return (await response.json()) as PredictionResponse[];
}

export async function deletePredictionHistory(id: string): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/api/predictions/history/${id}`, {
    method: "DELETE",
  });

  if (!response.ok) {
    await parseError(response);
  }
}
