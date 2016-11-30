import {
    Directive, ElementRef, OnChanges, Input, SimpleChanges,
    AfterViewInit
} from '@angular/core';

type ValidTarget = HTMLInputElement | HTMLTextAreaElement;

@Directive({
  selector: '[ttSelectAll]',
})
export class SelectAllDirective implements AfterViewInit, OnChanges {
  @Input() ttSelectAll: boolean;

  constructor(private elementRef: ElementRef) { }

  ngAfterViewInit(): void {
    this.select();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ('ttSelectAll' in changes && this.ttSelectAll) {
      this.select();
    }
  }

  private select(): void {
    let elem = this.elementRef.nativeElement as ValidTarget;
    window.setTimeout(() => {
      elem.select();
    }, 0);
  }
}
