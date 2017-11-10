import {Directive, OnInit, TemplateRef, ViewContainerRef} from '@angular/core';
import {CurrentUser} from '../security/current-user.service';

@Directive({
  selector: '[cfAdmin]'
})
export class AdminDirective implements OnInit {

  constructor(private currentUser: CurrentUser,
              private container: ViewContainerRef,
              private template: TemplateRef<any>) {
  }

  ngOnInit(): void {
    if (this.currentUser.isAdmin()) {
      this.container.createEmbeddedView(this.template);
    } else {
      this.container.clear();
    }
  }

}
