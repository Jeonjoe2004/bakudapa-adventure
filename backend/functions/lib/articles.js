"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.listArticles = exports.createArticle = void 0;
const v2_1 = require("firebase-functions/v2");
const firestore_1 = require("firebase-admin/firestore");
const db = (0, firestore_1.getFirestore)();
function isAdmin(req) {
    return req.auth?.token?.admin === true;
}
exports.createArticle = v2_1.https.onCall(async (req) => {
    if (!isAdmin(req))
        throw new v2_1.https.HttpsError('permission-denied', 'Admin only');
    const { title, content, author } = req.data || {};
    if (!title || typeof title !== 'string' || !title.trim())
        throw new v2_1.https.HttpsError('invalid-argument', 'title is required');
    if (!content || typeof content !== 'string' || !content.trim())
        throw new v2_1.https.HttpsError('invalid-argument', 'content is required');
    const article = {
        title: title.trim(),
        content: content.trim(),
        author: typeof author === 'string' ? author.trim() : 'Admin',
        published: false,
        createdAt: Date.now(),
    };
    const ref = await db.collection('articles').add(article);
    return { id: ref.id, ...article };
});
exports.listArticles = v2_1.https.onCall(async (req) => {
    if (!req.auth)
        throw new v2_1.https.HttpsError('unauthenticated', 'Login required');
    const snap = await db.collection('articles').orderBy('createdAt', 'desc').get();
    return snap.docs.map(d => ({ id: d.id, ...d.data() }));
});
//# sourceMappingURL=articles.js.map