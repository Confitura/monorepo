import {CookiesComponent} from './cookies.component';
import {FooterComponent} from './footer.component';
import {NavigationComponent} from './navigation/navigation.component';
import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {RouterModule} from '@angular/router';
import {MenuItemComponent} from './navigation/menu-item.component';

@NgModule({
  imports: [BrowserModule, RouterModule],
  declarations: [CookiesComponent, FooterComponent, NavigationComponent, MenuItemComponent],
  exports: [CookiesComponent, FooterComponent, NavigationComponent]
})
export class LayoutModule {

}
