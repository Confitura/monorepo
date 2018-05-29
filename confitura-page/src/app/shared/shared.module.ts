import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AdminDirective} from './admin.directive';
import {PhotoComponent} from './photo/photo.component';
import {SocialLinksComponent} from './social-links/social-links.component';
import {HttpClientModule} from '@angular/common/http';
import {LazySrcDirective} from './lazy-src.directive';
import {PageComponent} from './page/page.component';
import {PersonModalComponent} from './person-modal/person-modal.component';
import {PersonComponent} from './person/person.component';
import {PersonViewComponent} from './person-view/person-view.component';
import {FileUploadModule} from 'ng2-file-upload';
import {PersonModalService} from './person-modal/person-modal.service';
import {AdminOrOwnerDirective} from './admin-or-owner.directive';
import { ResizeImgPipe } from './resize-img.pipe';
import {MdPipe} from './md/md.pipe';
import { PageHeaderComponent } from './page-header/page-header.component';
import {BackButtonComponent} from './back-button/back-button.component';
import {MatButtonModule, MatCardModule, MatChipsModule, MatIconModule, MatMenuModule, MatTooltipModule} from '@angular/material';
import {PresentationComponent} from './presentation/presentation.component';
import { LikeButtonComponent } from './presentation/like/like-button/like-button.component';
import {LikeService} from './presentation/like/like.service';

@NgModule({
  imports: [
    CommonModule,
    FileUploadModule,
    FormsModule,
    MatIconModule,
    MatButtonModule,
    MatChipsModule,
    MatMenuModule,
    MatCardModule
  ],
  declarations: [
    AdminDirective,
    AdminOrOwnerDirective,
    PhotoComponent,
    SocialLinksComponent,
    LazySrcDirective,
    PageComponent,
    PersonModalComponent,
    PersonComponent,
    PersonViewComponent,
    ResizeImgPipe,
    MdPipe,
    PageHeaderComponent,
    BackButtonComponent,
    PresentationComponent,
    LikeButtonComponent
  ],
  exports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    AdminDirective,
    AdminOrOwnerDirective,
    PhotoComponent,
    SocialLinksComponent,
    LazySrcDirective,
    PageComponent,
    PersonModalComponent,
    PersonComponent,
    PersonViewComponent,
    ResizeImgPipe,
    MdPipe,
    PageHeaderComponent,
    BackButtonComponent,
    PresentationComponent,
    MatTooltipModule
  ],
  providers: [PersonModalService, LikeService]
})
export class SharedModule {

}
