export interface Mountain {
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
}
export interface Trail {
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
}
export interface AppUser {
    email: string;
    displayName?: string;
    photoUrl?: string;
    role: 'user' | 'admin';
    createdAt: number;
}
export interface DashboardStats {
    totalMountains: number;
    totalUsers: number;
    totalTrails: number;
    totalPosts: number;
    activeToday: number;
    updatedAt: number;
}
//# sourceMappingURL=types.d.ts.map