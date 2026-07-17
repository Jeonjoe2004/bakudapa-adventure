"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.deleteMountain = exports.updateMountain = exports.createMountain = exports.getMountain = exports.listMountains = void 0;
const v2_1 = require("firebase-functions/v2");
const firestore_1 = require("firebase-admin/firestore");
const db = (0, firestore_1.getFirestore)();
function isAdmin(req) {
    return req.auth?.token?.admin === true;
}
/** Validate required fields for a Mountain */
function validateMountain(data) {
    if (!data.name || typeof data.name !== 'string' || !data.name.trim())
        return 'name is required';
    if (!data.location || typeof data.location !== 'string' || !data.location.trim())
        return 'location is required';
    if (typeof data.elevation !== 'number' || data.elevation < 0 || !Number.isFinite(data.elevation))
        return 'elevation must be a positive number';
    if (data.imageUrl && typeof data.imageUrl !== 'string')
        return 'imageUrl must be a string';
    return null;
}
exports.listMountains = v2_1.https.onCall(async (req) => {
    if (!req.auth)
        throw new v2_1.https.HttpsError('unauthenticated', 'Login required');
    const snap = await db.collection('mountains').orderBy('name').get();
    return snap.docs.map(d => ({ id: d.id, ...d.data() }));
});
exports.getMountain = v2_1.https.onCall(async (req) => {
    if (!req.auth)
        throw new v2_1.https.HttpsError('unauthenticated', 'Login required');
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
    const err = validateMountain(req.data);
    if (err)
        throw new v2_1.https.HttpsError('invalid-argument', err);
    const data = {
        name: req.data.name.trim(),
        location: req.data.location.trim(),
        elevation: req.data.elevation,
        imageUrl: req.data.imageUrl || '',
        rating: typeof req.data.rating === 'number' ? req.data.rating : 0,
        latitude: typeof req.data.latitude === 'number' ? req.data.latitude : undefined,
        longitude: typeof req.data.longitude === 'number' ? req.data.longitude : undefined,
        description: req.data.description ? String(req.data.description) : undefined,
        difficulty: req.data.difficulty ? String(req.data.difficulty) : undefined,
        bestSeason: req.data.bestSeason ? String(req.data.bestSeason) : undefined,
        distance: typeof req.data.distance === 'number' ? req.data.distance : undefined,
        createdAt: Date.now(),
        updatedAt: Date.now(),
    };
    const ref = await db.collection('mountains').add(data);
    return { id: ref.id, ...data };
});
exports.updateMountain = v2_1.https.onCall(async (req) => {
    if (!isAdmin(req))
        throw new v2_1.https.HttpsError('permission-denied', 'Admin only');
    const id = req.data?.id;
    if (!id)
        throw new v2_1.https.HttpsError('invalid-argument', 'id required');
    const err = validateMountain({ ...req.data });
    if (err)
        throw new v2_1.https.HttpsError('invalid-argument', err);
    const { id: _id, ...rest } = req.data;
    const updates = { ...rest, updatedAt: Date.now() };
    await db.collection('mountains').doc(id).update(updates);
    return { id, ...updates };
});
exports.deleteMountain = v2_1.https.onCall(async (req) => {
    if (!isAdmin(req))
        throw new v2_1.https.HttpsError('permission-denied', 'Admin only');
    const id = req.data?.id;
    if (!id)
        throw new v2_1.https.HttpsError('invalid-argument', 'id required');
    await db.collection('mountains').doc(id).delete();
    return { deleted: id };
});
//# sourceMappingURL=mountains.js.map