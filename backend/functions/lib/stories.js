"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.cleanupExpiredStories = void 0;
const v2_1 = require("firebase-functions/v2");
const firestore_1 = require("firebase-admin/firestore");
const db = (0, firestore_1.getFirestore)();
/**
 * Scheduled function: hapus stories yang expired setiap jam
 */
exports.cleanupExpiredStories = v2_1.scheduler.onSchedule('0 * * * *', async () => {
    const now = Date.now();
    const expired = await db.collection('stories')
        .where('expiresAt', '<', now)
        .get();
    if (expired.empty) {
        console.log('No expired stories to clean');
        return;
    }
    let deleted = 0;
    for (const doc of expired.docs) {
        // Hapus subcollection viewers juga
        const viewers = await doc.ref.collection('viewers').get();
        const batch = db.batch();
        viewers.docs.forEach(v => batch.delete(v.ref));
        batch.delete(doc.ref);
        await batch.commit();
        deleted++;
    }
    console.log(`Cleaned ${deleted} expired stories`);
});
//# sourceMappingURL=stories.js.map