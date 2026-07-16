"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.setCustomClaims = exports.onUserCreated = void 0;
const v2_1 = require("firebase-functions/v2");
const firestore_1 = require("firebase-admin/firestore");
const auth_1 = require("firebase-admin/auth");
const params_1 = require("firebase-functions/params");
const adminEmails = (0, params_1.defineString)('ADMIN_EMAILS', { default: 'admin@bakudapa.com' });
const db = (0, firestore_1.getFirestore)();
/** Create user doc + set admin claims when user registers */
exports.onUserCreated = v2_1.identity.beforeUserCreated(async (event) => {
    const user = event.data;
    if (!user || !user.email)
        return;
    const isAdmin = adminEmails.value().split(',').includes(user.email);
    const appUser = {
        email: user.email,
        displayName: user.displayName || user.email.split('@')[0],
        photoUrl: user.photoURL || undefined,
        role: isAdmin ? 'admin' : 'user',
        createdAt: Date.now(),
    };
    await db.collection('users').doc(user.uid).set(appUser);
    if (isAdmin) {
        await (0, auth_1.getAuth)().setCustomUserClaims(user.uid, { admin: true });
    }
});
/** Fallback: set admin claims when user doc is created (catches manual doc writes) */
exports.setCustomClaims = v2_1.firestore.onDocumentCreated('users/{userId}', async (event) => {
    const data = event.data?.data();
    if (!data?.email)
        return;
    if (data.role === 'admin') {
        await (0, auth_1.getAuth)().setCustomUserClaims(event.params.userId, { admin: true });
    }
});
//# sourceMappingURL=auth.js.map