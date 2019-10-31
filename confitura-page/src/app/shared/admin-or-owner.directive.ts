import {Directive, Input, OnInit, TemplateRef, ViewContainerRef} from '@angular/core';
import {CurrentUser} from '../core/security/current-user.service';

@Directive({
  selector: '[cfAdminOrOwner]'
})
// TODO: this should be merged with AdminDirective
export class AdminOrOwnerDirective implements OnInit {

  @Input()
  cfAdminOrOwner: string;

  constructor(private currentUser: CurrentUser,
              private container: ViewContainerRef,
              private template: TemplateRef<any>) {
  }

  ngOnInit(): void {
    if (this.currentUser.isAdmin() || this.currentUser.is(this.cfAdminOrOwner)) {
      this.container.createEmbeddedView(this.template);
    } else {
      this.container.clear();
    }
  }

}
