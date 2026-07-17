import { firestore } from 'firebase-functions/v2';
/** Kirim notif saat user dapat like baru */
export declare const onPostLiked: import("firebase-functions/core").CloudFunction<firestore.FirestoreEvent<firestore.QueryDocumentSnapshot | undefined, {
    userId: string;
    postId: string;
}>>;
/** Kirim notif saat user dapat komentar baru */
export declare const onPostCommented: import("firebase-functions/core").CloudFunction<firestore.FirestoreEvent<firestore.QueryDocumentSnapshot | undefined, {
    postId: string;
    commentId: string;
}>>;
/** Kirim notif saat user di-follow */
export declare const onFollowed: import("firebase-functions/core").CloudFunction<firestore.FirestoreEvent<firestore.QueryDocumentSnapshot | undefined, {
    targetId: string;
    followerId: string;
}>>;
//# sourceMappingURL=messaging.d.ts.map