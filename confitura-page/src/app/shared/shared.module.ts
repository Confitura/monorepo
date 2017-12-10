import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {AdminDirective} from './admin.directive';
import {PhotoComponent} from './photo/photo.component';
import {SocialLinksComponent} from './social-links/social-links.component';
import {HttpClientModule} from '@angular/common/http';
import {LazySrcDirective} from './lazy-src.directive';
import {PageComponent} from './page/page.component';

@NgModule({
  imports: [CommonModule, HttpClientModule],
  declarations: [AdminDirective, PhotoComponent, SocialLinksComponent, LazySrcDirective, PageComponent],
  exports: [CommonModule, FormsModule, RouterModule, AdminDirective, PhotoComponent,
    SocialLinksComponent, LazySrcDirective, PageComponent]
})
export class SharedModule {

}
