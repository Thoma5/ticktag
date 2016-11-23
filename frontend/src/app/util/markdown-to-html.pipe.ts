import { Pipe, PipeTransform } from '@angular/core';
import { MarkdownService } from '../service';

@Pipe({ name: 'ttMarkdownToHtml' })
export class MarkdownToHtmlPipe implements PipeTransform {
    constructor(private markdownService: MarkdownService) { }

    transform(value: string): string {
        return this.markdownService.markdownToHtml(value);
    }
}
