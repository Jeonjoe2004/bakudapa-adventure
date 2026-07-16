import { https } from 'firebase-functions/v2';
export declare const listMountains: https.CallableFunction<any, Promise<{
    id: string;
}[]>, unknown>;
export declare const getMountain: https.CallableFunction<any, Promise<{
    id: string;
}>, unknown>;
export declare const createMountain: https.CallableFunction<any, Promise<{
    name: string;
    location: string;
    elevation: number;
    latitude?: number;
    longitude?: number;
    imageUrl: string;
    rating: number;
    description?: string;
    difficulty?: string;
    bestSeason?: string;
    distance?: number;
    createdAt: number;
    updatedAt: number;
    id: string;
}>, unknown>;
export declare const updateMountain: https.CallableFunction<any, Promise<any>, unknown>;
export declare const deleteMountain: https.CallableFunction<any, Promise<{
    deleted: string;
}>, unknown>;
//# sourceMappingURL=mountains.d.ts.map