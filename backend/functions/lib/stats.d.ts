import { https, scheduler } from 'firebase-functions/v2';
export declare const getDashboardStats: https.CallableFunction<any, Promise<{
    totalMountains: number;
    totalUsers: number;
    totalTrails: number;
    totalPosts: number;
    activeToday: number;
}>, unknown>;
/** Refresh stats doc every 30 min */
export declare const scheduledStatsRefresh: scheduler.ScheduleFunction;
//# sourceMappingURL=stats.d.ts.map