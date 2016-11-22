import { Component, Input } from '@angular/core';
import { TextviewReadComponent } from '../edit-textview/edit-textview.component';

@Component({
    selector: 'tt-markdown-textview-read',
    template: `<div class='markdown-textview markdown-body' [innerHTML]='content|ttMarkdownToHtml'></div>`,
    styleUrls: ['./markdown-textview-read.component.scss'],
})
export class MarkdownTextviewReadComponent implements TextviewReadComponent<string> {
    @Input() content: string;
}
