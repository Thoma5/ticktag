import { ElementRef, HostListener, Directive, AfterContentChecked } from '@angular/core';

@Directive({
    selector: '[ttAutosize]'
})
export class AutosizeTextareaDirective implements AfterContentChecked {
    constructor(private element: ElementRef) { }

    @HostListener('input', ['$event.target'])
    onInput(textArea: HTMLTextAreaElement): void {
        this.adjust();
    }

    ngAfterContentChecked(): void {
        this.adjust();
    }

    adjust(): void {
        this.element.nativeElement.style.overflow = 'hidden';
        this.element.nativeElement.style.height = 'auto';
        this.element.nativeElement.style.height = this.element.nativeElement.scrollHeight + 'px';
    }
}
