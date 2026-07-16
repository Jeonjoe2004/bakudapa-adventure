"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.scheduledStatsRefresh = exports.getDashboardStats = void 0;
const v2_1 = require("firebase-functions/v2");
const firestore_1 = require("firebase-admin/firestore");
const db = (0, firestore_1.getFirestore)();
exports.getDashboardStats = v2_1.https.onCall(async () => {
    const [mSnap, uSnap, tSnap, pSnap] = await Promise.all([
        db.collection('mountains').count().get(),
        db.collection('users').count().get(),
        db.collection('trails').count().get(),
        db.collection('posts').count().get(),
    ]);
    return {
        totalMountains: mSnap.data().count,
        totalUsers: uSnap.data().count,
        totalTrails: tSnap.data().count,
        totalPosts: pSnap.data().count,
        activeToday: 0,
    };
});
/** Refresh stats doc every 30 min */
exports.scheduledStatsRefresh = v2_1.scheduler.onSchedule('*/30 * * * *', async () => {
    const [mSnap, uSnap, tSnap, pSnap] = await Promise.all([
        db.collection('mountains').count().get(),
        db.collection('users').count().get(),
        db.collection('trails').count().get(),
        db.collection('posts').count().get(),
    ]);
    const stats = {
        totalMountains: mSnap.data().count,
        totalUsers: uSnap.data().count,
        totalTrails: tSnap.data().count,
        totalPosts: pSnap.data().count,
        activeToday: 0,
        updatedAt: Date.now(),
    };
    await db.collection('stats').doc('dashboard').set(stats);
});
//# sourceMappingURL=stats.js.map