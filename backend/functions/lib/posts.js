"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.deletePost = void 0;
const v2_1 = require("firebase-functions/v2");
const firestore_1 = require("firebase-admin/firestore");
const db = (0, firestore_1.getFirestore)();
exports.deletePost = v2_1.https.onCall(async (req) => {
    if (req.auth?.token?.admin !== true) {
        throw new v2_1.https.HttpsError('permission-denied', 'Admin only');
    }
    const id = req.data?.id;
    if (!id)
        throw new v2_1.https.HttpsError('invalid-argument', 'id required');
    await db.collection('posts').doc(id).delete();
    return { deleted: id };
});
//# sourceMappingURL=posts.js.map