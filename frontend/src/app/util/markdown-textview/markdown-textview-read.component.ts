import { Component, Input, OnChanges, Compiler } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { htmlifyCommands } from '../../service/command/grammar';
import { MarkdownService } from '../../service/markdown/markdown.service';

@Component({
    selector: 'tt-markdown-textview-read',
    template: `<div class='markdown-textview markdown-body' [innerHTML]='parsedContent'></div>`,
    styleUrls: ['./markdown-textview-read.component.scss'],
})
export class MarkdownTextviewReadComponent implements OnChanges {
    @Input() content: string;
    @Input() commands = false;
    @Input() projectId: string;

    private parsedContent: SafeHtml = '';

    constructor(
        private domSanitizer: DomSanitizer,
        private markdownService: MarkdownService,
        private compiler: Compiler) {}

    ngOnChanges() {
        let s = this.markdownService.markdownToHtml(this.content);
        if (this.commands) {
            s = htmlifyCommands(s, this.projectId);
        }
        this.parsedContent = this.domSanitizer.bypassSecurityTrustHtml(s);
    }
}
