import {Directive, OnInit, TemplateRef, ViewContainerRef} from '@angular/core';
import {CurrentUser} from '../core/security/current-user.service';

@Directive({
  selector: '[cfAuthenticated]'
})
export class AuthenticatedDirective implements OnInit {

  constructor(private currentUser: CurrentUser,
              private container: ViewContainerRef,
              private template: TemplateRef<any>) {
  }

  ngOnInit(): void {
    if (this.currentUser.isAvailable()) {
      this.container.createEmbeddedView(this.template);
    } else {
      this.container.clear();
    }
  }

}
