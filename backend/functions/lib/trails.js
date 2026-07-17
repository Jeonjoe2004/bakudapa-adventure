"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.deleteTrail = exports.updateTrail = exports.createTrail = exports.getTrail = exports.listTrails = void 0;
const v2_1 = require("firebase-functions/v2");
const firestore_1 = require("firebase-admin/firestore");
const db = (0, firestore_1.getFirestore)();
function isAdmin(req) {
    return req.auth?.token?.admin === true;
}
/** Validate required fields for a Trail */
function validateTrail(data) {
    if (!data.name || typeof data.name !== 'string' || !data.name.trim())
        return 'name is required';
    if (!data.mountainId || typeof data.mountainId !== 'string')
        return 'mountainId is required';
    if (!data.mountainName || typeof data.mountainName !== 'string' || !data.mountainName.trim())
        return 'mountainName is required';
    const validDiffs = ['EASY', 'MODERATE', 'HARD', 'EXPERT'];
    if (data.difficulty && !validDiffs.includes(String(data.difficulty)))
        return `difficulty must be one of: ${validDiffs.join(', ')}`;
    if (data.distanceKm != null && (typeof data.distanceKm !== 'number' || data.distanceKm < 0))
        return 'distanceKm must be a positive number';
    if (data.durationMinutes != null && (typeof data.durationMinutes !== 'number' || data.durationMinutes < 0))
        return 'durationMinutes must be a positive number';
    return null;
}
exports.listTrails = v2_1.https.onCall(async (req) => {
    if (!req.auth)
        throw new v2_1.https.HttpsError('unauthenticated', 'Login required');
    let query = db.collection('trails').orderBy('name');
    if (req.data?.mountainId)
        query = query.where('mountainId', '==', req.data.mountainId);
    const snap = await query.get();
    return snap.docs.map(d => ({ id: d.id, ...d.data() }));
});
exports.getTrail = v2_1.https.onCall(async (req) => {
    if (!req.auth)
        throw new v2_1.https.HttpsError('unauthenticated', 'Login required');
    const id = req.data?.id;
    if (!id)
        throw new v2_1.https.HttpsError('invalid-argument', 'id required');
    const doc = await db.collection('trails').doc(id).get();
    if (!doc.exists)
        throw new v2_1.https.HttpsError('not-found', 'Trail not found');
    return { id: doc.id, ...doc.data() };
});
exports.createTrail = v2_1.https.onCall(async (req) => {
    if (!isAdmin(req))
        throw new v2_1.https.HttpsError('permission-denied', 'Admin only');
    const err = validateTrail(req.data);
    if (err)
        throw new v2_1.https.HttpsError('invalid-argument', err);
    const data = {
        name: req.data.name.trim(),
        mountainId: req.data.mountainId,
        mountainName: req.data.mountainName.trim(),
        difficulty: req.data.difficulty || 'MODERATE',
        distanceKm: typeof req.data.distanceKm === 'number' ? req.data.distanceKm : 0,
        durationMinutes: typeof req.data.durationMinutes === 'number' ? req.data.durationMinutes : 0,
        imageUrl: req.data.imageUrl || '',
        description: req.data.description ? String(req.data.description) : undefined,
        elevationGain: typeof req.data.elevationGain === 'number' ? req.data.elevationGain : undefined,
        maxElevation: typeof req.data.maxElevation === 'number' ? req.data.maxElevation : undefined,
        popularity: 0,
        createdAt: Date.now(),
    };
    const ref = await db.collection('trails').add(data);
    return { id: ref.id, ...data };
});
exports.updateTrail = v2_1.https.onCall(async (req) => {
    if (!isAdmin(req))
        throw new v2_1.https.HttpsError('permission-denied', 'Admin only');
    const id = req.data?.id;
    if (!id)
        throw new v2_1.https.HttpsError('invalid-argument', 'id required');
    const err = validateTrail({ ...req.data });
    if (err)
        throw new v2_1.https.HttpsError('invalid-argument', err);
    const { id: _id, ...rest } = req.data;
    await db.collection('trails').doc(id).update(rest);
    return { id, ...rest };
});
exports.deleteTrail = v2_1.https.onCall(async (req) => {
    if (!isAdmin(req))
        throw new v2_1.https.HttpsError('permission-denied', 'Admin only');
    const id = req.data?.id;
    if (!id)
        throw new v2_1.https.HttpsError('invalid-argument', 'id required');
    await db.collection('trails').doc(id).delete();
    return { deleted: id };
});
//# sourceMappingURL=trails.js.map