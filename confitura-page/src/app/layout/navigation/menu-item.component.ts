import {Component, Input} from '@angular/core';
import {MenuItem} from './menu-item.model';

@Component({
  selector: '[cf-menu-item]',
  templateUrl: './menu-item.component.html'
})
export class MenuItemComponent {
  @Input()
  model: MenuItem;

  isVisible(): boolean {
    return !this.model.show || this.model.show() !== false;
  }

  closeMenu() {
    $('#navbar').collapse('hide');

  }

  click() {
    this.model.action();
    this.closeMenu();
  }

}
