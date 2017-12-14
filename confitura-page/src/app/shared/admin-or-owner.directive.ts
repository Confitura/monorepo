import {Directive, Input, OnInit, TemplateRef, ViewContainerRef} from '@angular/core';
import {CurrentUser} from '../security/current-user.service';

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
    console.log(this.cfAdminOrOwner);
    if (this.currentUser.isAdmin() || this.currentUser.is(this.cfAdminOrOwner)) {
      this.container.createEmbeddedView(this.template);
    } else {
      this.container.clear();
    }
  }

}
