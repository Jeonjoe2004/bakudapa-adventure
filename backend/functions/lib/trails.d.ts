import { https } from 'firebase-functions/v2';
export declare const listTrails: https.CallableFunction<any, Promise<{
    id: string;
}[]>, unknown>;
export declare const getTrail: https.CallableFunction<any, Promise<{
    id: string;
}>, unknown>;
export declare const createTrail: https.CallableFunction<any, Promise<{
    name: string;
    mountainId: string;
    mountainName: string;
    difficulty: string;
    distanceKm: number;
    durationMinutes: number;
    imageUrl: string;
    description?: string;
    elevationGain?: number;
    maxElevation?: number;
    popularity: number;
    createdAt: number;
    id: string;
}>, unknown>;
export declare const updateTrail: https.CallableFunction<any, Promise<any>, unknown>;
export declare const deleteTrail: https.CallableFunction<any, Promise<{
    deleted: string;
}>, unknown>;
//# sourceMappingURL=trails.d.ts.map