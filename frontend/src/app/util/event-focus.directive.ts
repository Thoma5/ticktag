import {
  Directive, ElementRef, OnDestroy, OnChanges, Input, SimpleChanges
} from '@angular/core';
import { Observable, Subscription } from 'rxjs';

@Directive({
  selector: '[ttEventFocus]',
})
export class EventFocusDirective implements OnChanges, OnDestroy {
  @Input() ttEventFocus: Observable<void> | null = null;
  private subscription: Subscription | null = null;

  constructor(private elementRef: ElementRef) { }

  ngOnChanges(changes: SimpleChanges): void {
    if ('ttEventFocus' in changes) {
      this.resubscribeFocusEvent();
    }
  }

  ngOnDestroy(): void {
    if (this.subscription != null) {
      this.subscription.unsubscribe();
    }
  }

  private resubscribeFocusEvent() {
    if (this.subscription != null) {
      this.subscription.unsubscribe();
    }
    if (this.ttEventFocus != null) {
      this.subscription = this.ttEventFocus.subscribe(_ => {
        this.elementRef.nativeElement.focus();
      });
    }
  }
}
