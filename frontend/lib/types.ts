export type PredictionFormData = {
  age: number;
  bmi: number;
  systolicBp: number;
  diastolicBp: number;
  familyHistory: boolean;
  smokingStatus: "Never" | "Former" | "Current";
  physicalActivityLevel: "Low" | "Moderate" | "High";
  diabetes: boolean;
};

export type PredictionInput = {
  age: number;
  bmi: number;
  systolicBp: number;
  diastolicBp: number;
  familyHistory: boolean;
  smokingStatus: string;
  physicalActivityLevel: string | number;
  diabetes: boolean;
};

export type PredictionRisk = {
  percentage: number;
  category: "low" | "medium" | "high";
  note: string;
  suggestions: string[];
};

export type PredictionResponse = {
  id: string;
  createdAt: string;
  input?: PredictionInput | null;
  hypertension: PredictionRisk;
  cardiovascular: PredictionRisk;
};

export type ApiError = {
  message?: string;
  errors?: Record<string, string>;
};