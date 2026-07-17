"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const vitest_1 = require("vitest");
(0, vitest_1.describe)('types', () => {
    (0, vitest_1.it)('Mountain shape is valid', () => {
        const m = {
            name: 'Semeru',
            location: 'Jawa Timur',
            elevation: 3676,
            latitude: -8.1078,
            longitude: 112.9225,
            imageUrl: 'https://example.com/semeru.jpg',
            rating: 4.8,
            description: 'Gunung tertinggi di Jawa',
            difficulty: 'HARD',
            bestSeason: 'Apr-Oct',
            distance: 12.5,
            createdAt: Date.now(),
            updatedAt: Date.now(),
        };
        (0, vitest_1.expect)(m.name).toBe('Semeru');
        (0, vitest_1.expect)(m.elevation).toBeGreaterThan(0);
    });
    (0, vitest_1.it)('Trail shape is valid', () => {
        const t = {
            name: 'Ranupane',
            mountainId: 'semeru_1',
            mountainName: 'Semeru',
            difficulty: 'MODERATE',
            distanceKm: 12.0,
            durationMinutes: 480,
            imageUrl: 'https://example.com/trail.jpg',
            elevationGain: 1500,
            maxElevation: 3676,
            popularity: 85,
            createdAt: Date.now(),
        };
        (0, vitest_1.expect)(t.name).toBeTruthy();
        (0, vitest_1.expect)(t.distanceKm).toBeGreaterThan(0);
    });
    (0, vitest_1.it)('AppUser defaults to user role', () => {
        const u = {
            email: 'test@bakudapa.com',
            displayName: 'Test User',
            role: 'user',
            createdAt: Date.now(),
        };
        (0, vitest_1.expect)(u.role).toBe('user');
    });
    (0, vitest_1.it)('DashboardStats has activeToday placeholder', () => {
        const s = {
            totalMountains: 5,
            totalUsers: 100,
            totalTrails: 20,
            totalPosts: 45,
            activeToday: 0,
            updatedAt: Date.now(),
        };
        (0, vitest_1.expect)(s.activeToday).toBe(0);
        (0, vitest_1.expect)(s.totalMountains + s.totalTrails).toBeGreaterThan(0);
    });
});
//# sourceMappingURL=types.test.js.map