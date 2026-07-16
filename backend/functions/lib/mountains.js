"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.deleteMountain = exports.updateMountain = exports.createMountain = exports.getMountain = exports.listMountains = void 0;
const v2_1 = require("firebase-functions/v2");
const firestore_1 = require("firebase-admin/firestore");
const db = (0, firestore_1.getFirestore)();
function isAdmin(req) {
    return req.auth?.token?.admin === true;
}
exports.listMountains = v2_1.https.onCall(async () => {
    const snap = await db.collection('mountains').orderBy('name').get();
    return snap.docs.map(d => ({ id: d.id, ...d.data() }));
});
exports.getMountain = v2_1.https.onCall(async (req) => {
    const id = req.data?.id;
    if (!id)
        throw new v2_1.https.HttpsError('invalid-argument', 'id required');
    const doc = await db.collection('mountains').doc(id).get();
    if (!doc.exists)
        throw new v2_1.https.HttpsError('not-found', 'Mountain not found');
    return { id: doc.id, ...doc.data() };
});
exports.createMountain = v2_1.https.onCall(async (req) => {
    if (!isAdmin(req))
        throw new v2_1.https.HttpsError('permission-denied', 'Admin only');
    const data = {
        name: req.data.name,
        location: req.data.location,
        elevation: req.data.elevation,
        imageUrl: req.data.imageUrl || '',
        rating: req.data.rating || 0,
        latitude: req.data.latitude,
        longitude: req.data.longitude,
        description: req.data.description,
        difficulty: req.data.difficulty,
        bestSeason: req.data.bestSeason,
        distance: req.data.distance,
        createdAt: Date.now(),
        updatedAt: Date.now(),
    };
    const ref = await db.collection('mountains').add(data);
    return { id: ref.id, ...data };
});
exports.updateMountain = v2_1.https.onCall(async (req) => {
    if (!isAdmin(req))
        throw new v2_1.https.HttpsError('permission-denied', 'Admin only');
    const id = req.data.id;
    if (!id)
        throw new v2_1.https.HttpsError('invalid-argument', 'id required');
    const updates = { ...req.data, id: undefined, updatedAt: Date.now() };
    await db.collection('mountains').doc(id).update(updates);
    return { id, ...updates };
});
exports.deleteMountain = v2_1.https.onCall(async (req) => {
    if (!isAdmin(req))
        throw new v2_1.https.HttpsError('permission-denied', 'Admin only');
    const id = req.data.id;
    if (!id)
        throw new v2_1.https.HttpsError('invalid-argument', 'id required');
    await db.collection('mountains').doc(id).delete();
    return { deleted: id };
});
//# sourceMappingURL=mountains.js.map