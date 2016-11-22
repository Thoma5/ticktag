import { Injectable } from '@angular/core';
import * as marked from 'marked';

@Injectable()
export class MarkdownService {
    private readonly parser: MarkedStatic;

    constructor() {
        this.parser = marked.setOptions({});
    }

    markdownToHtml(markdown: string): string {
        return this.parser.parse(markdown);
    }
}
