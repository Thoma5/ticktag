import { Component, Input } from '@angular/core';
import { TextviewReadComponent } from '../editable-textview/editable-textview.component';

@Component({
    selector: 'tt-markdown-textview-read',
    template: `<div class='markdown-textview markdown-body' [innerHTML]='text|ttMarkdownToHtml'></div>`,
    styleUrls: ['ticket-markdown-textview-read.component.scss'],
})
export class MarkdownTextviewReadComponent implements TextviewReadComponent {
    @Input() text: string;
}
