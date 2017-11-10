import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {LoginService} from '../security/login.service';
import {CurrentUser} from '../security/current-user.service';
import {RouterModule} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {AdminDirective} from './admin.directive';
import {ImageResizer} from './ImageResizer.service';
import {PhotoComponent} from './photo/photo.component';
import {SocialLinksComponent} from './social-links/social-links.component';
import {HttpClientModule} from '@angular/common/http';
import {LazySrcDirective} from './lazy-src.directive';

@NgModule({
  imports: [CommonModule, HttpClientModule],
  declarations: [AdminDirective, PhotoComponent, SocialLinksComponent, LazySrcDirective],
  providers: [LoginService, CurrentUser, ImageResizer],
  exports: [CommonModule, FormsModule, RouterModule, AdminDirective, PhotoComponent, SocialLinksComponent, LazySrcDirective]
})
export class SharedModule {

}
