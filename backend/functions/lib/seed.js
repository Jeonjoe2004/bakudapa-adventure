"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.seedData = void 0;
const v2_1 = require("firebase-functions/v2");
const firestore_1 = require("firebase-admin/firestore");
const auth_1 = require("firebase-admin/auth");
const db = (0, firestore_1.getFirestore)();
// eslint-disable-next-line @typescript-eslint/no-var-requires
const mountains = require('../../../seed_mountains.json');
// eslint-disable-next-line @typescript-eslint/no-var-requires
const trails = require('../../../seed_trails.json');
exports.seedData = v2_1.https.onCall(async () => {
    const results = [];
    // --- Seed mountains ---
    const mCol = db.collection('mountains');
    const mSnap = await mCol.get();
    if (mSnap.size === 0) {
        for (const m of mountains) {
            await mCol.add({ ...m, updatedAt: Date.now() });
        }
        results.push(`Seeded ${mountains.length} mountains`);
    }
    else {
        results.push(`Skipped mountains: ${mSnap.size} already exist`);
    }
    // --- Seed admin user ---
    try {
        const userRecord = await (0, auth_1.getAuth)().createUser({
            email: 'admin@bakudapa.com',
            password: 'admin123',
            displayName: 'Admin Bakudapa',
        });
        await db.collection('users').doc(userRecord.uid).set({
            email: 'admin@bakudapa.com',
            displayName: 'Admin Bakudapa',
            role: 'admin',
            lastActiveAt: Date.now(),
            createdAt: Date.now(),
        });
        await (0, auth_1.getAuth)().setCustomUserClaims(userRecord.uid, { admin: true });
        results.push('Created admin user: admin@bakudapa.com / admin123');
    }
    catch (e) {
        if (e.code === 'auth/email-already-exists') {
            results.push('Admin user already exists');
        }
        else {
            throw e;
        }
    }
    // --- Seed trails ---
    const tCol = db.collection('trails');
    const tSnap = await tCol.get();
    if (tSnap.size === 0) {
        for (const t of trails) {
            await tCol.add(t);
        }
        results.push(`Seeded ${trails.length} trails`);
    }
    else {
        results.push(`Skipped trails: ${tSnap.size} already exist`);
    }
    return { success: true, results };
});
//# sourceMappingURL=seed.js.map