import { Directive, ElementRef, AfterViewInit, OnChanges, Input, SimpleChanges } from '@angular/core';

@Directive({
    selector: '[ttFocus]',
})
export class FocusDirective implements AfterViewInit, OnChanges {
    @Input('focus') focus: boolean;

    constructor(private elementRef: ElementRef) { }

    ngAfterViewInit() {
        this.elementRef.nativeElement.focus();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['focus'] && changes['focus'].currentValue === true) {
            this.elementRef.nativeElement.focus();
        }
    }
}
