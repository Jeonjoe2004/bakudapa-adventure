import { https } from 'firebase-functions/v2';
export declare const createArticle: https.CallableFunction<any, Promise<{
    title: string;
    content: string;
    author: string;
    published: boolean;
    createdAt: number;
    updatedAt?: number;
    id: string;
}>, unknown>;
export declare const listArticles: https.CallableFunction<any, Promise<{
    id: string;
}[]>, unknown>;
//# sourceMappingURL=articles.d.ts.map