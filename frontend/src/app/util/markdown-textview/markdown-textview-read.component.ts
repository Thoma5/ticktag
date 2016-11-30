import { Component, Input } from '@angular/core';

@Component({
    selector: 'tt-markdown-textview-read',
    template: `<div class='markdown-textview markdown-body' [innerHTML]='content|ttMarkdownToHtml'></div>`,
    styleUrls: ['./markdown-textview-read.component.scss'],
})
export class MarkdownTextviewReadComponent {
    @Input() content: string;
}
