"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.deleteTrail = exports.updateTrail = exports.createTrail = exports.getTrail = exports.listTrails = void 0;
const v2_1 = require("firebase-functions/v2");
const firestore_1 = require("firebase-admin/firestore");
const db = (0, firestore_1.getFirestore)();
function isAdmin(req) {
    return req.auth?.token?.admin === true;
}
exports.listTrails = v2_1.https.onCall(async (req) => {
    let query = db.collection('trails').orderBy('name');
    if (req.data?.mountainId)
        query = query.where('mountainId', '==', req.data.mountainId);
    const snap = await query.get();
    return snap.docs.map(d => ({ id: d.id, ...d.data() }));
});
exports.getTrail = v2_1.https.onCall(async (req) => {
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
    const data = {
        name: req.data.name,
        mountainId: req.data.mountainId,
        mountainName: req.data.mountainName,
        difficulty: req.data.difficulty || 'MODERATE',
        distanceKm: req.data.distanceKm || 0,
        durationMinutes: req.data.durationMinutes || 0,
        imageUrl: req.data.imageUrl || '',
        description: req.data.description,
        elevationGain: req.data.elevationGain,
        maxElevation: req.data.maxElevation,
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
    const updates = { ...req.data, id: undefined };
    await db.collection('trails').doc(id).update(updates);
    return { id, ...updates };
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