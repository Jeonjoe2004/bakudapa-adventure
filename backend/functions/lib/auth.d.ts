import { firestore } from 'firebase-functions/v2';
import type { BlockingFunction } from 'firebase-functions/v1';
/** Create user doc + set admin claims when user registers */
export declare const onUserCreated: BlockingFunction;
/** Fallback: set admin claims when user doc is created (catches manual doc writes) */
export declare const setCustomClaims: import("firebase-functions/core").CloudFunction<firestore.FirestoreEvent<firestore.QueryDocumentSnapshot | undefined, {
    userId: string;
}>>;
//# sourceMappingURL=auth.d.ts.map