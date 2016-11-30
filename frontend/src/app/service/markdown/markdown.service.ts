import { Injectable } from '@angular/core';

@Injectable()
export class MarkdownService {
    private markdown: any;

    constructor() {
        this.markdown = require('markdown-it')({
            'commonmark': true,
            'html': false,
            'linkify': true,
        });
    }

    markdownToHtml(markdown: string): string {
        let html = this.markdown.render(markdown);
        return html;
    }
}
