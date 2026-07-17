"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.onFollowed = exports.onPostCommented = exports.onPostLiked = void 0;
const firestore_1 = require("firebase-admin/firestore");
const messaging_1 = require("firebase-admin/messaging");
const v2_1 = require("firebase-functions/v2");
const params_1 = require("firebase-functions/params");
const db = (0, firestore_1.getFirestore)();
const FCM_SERVER_KEY = (0, params_1.defineString)('FCM_SERVER_KEY', { default: '' });
/**
 * Helper: kirim notif ke semua FCM token user tertentu
 */
async function sendToUser(targetUid, title, body, data) {
    try {
        const tokensSnap = await db.collection('users').doc(targetUid)
            .collection('fcmTokens').get();
        const tokens = tokensSnap.docs.map(d => d.data().token).filter(Boolean);
        if (tokens.length === 0)
            return;
        // Send pake Admin SDK (works with Firebase project credentials)
        const message = {
            notification: { title, body },
            data,
            tokens,
        };
        await (0, messaging_1.getMessaging)().sendEachForMulticast(message);
    }
    catch (err) {
        console.error(`Failed to send notification to ${targetUid}:`, err);
    }
}
/** Kirim notif saat user dapat like baru */
exports.onPostLiked = v2_1.firestore.onDocumentCreated('posts/{postId}/likes/{userId}', async (event) => {
    const likerId = event.params.userId;
    const postId = event.params.postId;
    // Dapatkan author post
    const postSnap = await db.collection('posts').doc(postId).get();
    const authorId = postSnap.get('authorId');
    if (!authorId || authorId === likerId)
        return;
    // Nama pengirim
    const likerSnap = await db.collection('users').doc(likerId).get();
    const likerName = likerSnap.get('displayName') || 'Someone';
    await sendToUser(authorId, '❤️ Like Baru', `${likerName} menyukai postinganmu`, { type: 'like', targetId: postId, from: likerId });
});
/** Kirim notif saat user dapat komentar baru */
exports.onPostCommented = v2_1.firestore.onDocumentCreated('posts/{postId}/comments/{commentId}', async (event) => {
    const commenterId = event.data?.data().authorId;
    const postId = event.params.postId;
    const postSnap = await db.collection('posts').doc(postId).get();
    const authorId = postSnap.get('authorId');
    if (!authorId || authorId === commenterId)
        return;
    const commenterSnap = await db.collection('users').doc(commenterId).get();
    const commenterName = commenterSnap.get('displayName') || 'Someone';
    await sendToUser(authorId, '💬 Komentar Baru', `${commenterName} berkomentar di postinganmu`, { type: 'comment', targetId: postId, from: commenterId });
});
/** Kirim notif saat user di-follow */
exports.onFollowed = v2_1.firestore.onDocumentCreated('users/{targetId}/followers/{followerId}', async (event) => {
    const followerId = event.params.followerId;
    const targetId = event.params.targetId;
    const followerSnap = await db.collection('users').doc(followerId).get();
    const followerName = followerSnap.get('displayName') || 'Someone';
    await sendToUser(targetId, '👥 Pengikut Baru', `${followerName} mulai mengikutimu`, { type: 'follow', targetId: followerId, from: followerId });
});
//# sourceMappingURL=messaging.js.map