import {
    Directive, ElementRef, AfterViewInit, OnChanges, Input, SimpleChanges
} from '@angular/core';

@Directive({
    selector: '[ttFocus]',
})
export class FocusDirective implements AfterViewInit, OnChanges {
    @Input() ttFocus: boolean;

    constructor(private elementRef: ElementRef) { }

    ngAfterViewInit(): void {
        this.elementRef.nativeElement.focus();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['ttFocus'] && changes['ttFocus'].currentValue === true) {
            this.elementRef.nativeElement.focus();
        }
    }
}
