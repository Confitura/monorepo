import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AdminDirective} from './admin.directive';
import {PhotoComponent} from './photo/photo.component';
import {SocialLinksComponent} from './social-links/social-links.component';
import {LazySrcDirective} from './lazy-src.directive';
import {PageComponent} from './page/page.component';
import {PersonModalComponent} from './person-modal/person-modal.component';
import {PersonComponent} from './person/person.component';
import {PersonViewComponent} from './person-view/person-view.component';
import {FileUploadModule} from 'ng2-file-upload';
import {PersonModalService} from './person-modal/person-modal.service';
import {AdminOrOwnerDirective} from './admin-or-owner.directive';
import {ResizeImgPipe} from './resize-img.pipe';
import {MdPipe} from './md/md.pipe';
import {PageHeaderComponent} from './page-header/page-header.component';
import {BackButtonComponent} from './back-button/back-button.component';
import {
  MatButtonModule,
  MatCardModule,
  MatChipsModule,
  MatDialogModule,
  MatExpansionModule,
  MatFormFieldModule,
  MatIconModule,
  MatInputModule,
  MatMenuModule,
  MatProgressSpinnerModule,
  MatSliderModule,
  MatSnackBarModule,
  MatTooltipModule
} from '@angular/material';
import {PresentationComponent} from './presentation/presentation.component';
import {LikeButtonComponent} from './presentation/like/like-button/like-button.component';
import {LikeService} from './presentation/like/like.service';
import {PresentationMetadataComponent} from './presentation/presentation-metadata/presentation-metadata.component';
import {RatePresentationComponent} from './presentation/rate-presentation/rate-presentation.component';
import {RatePresentationDialogComponent} from './presentation/rate-presentation/rate-presentation-dialog/rate-presentation-dialog.component';
import {RateSliderComponent} from './presentation/rate-presentation/rate-slider/rate-slider.component';
import {RatePresentationDialogDirective} from './presentation/rate-presentation/rate-presentation-dialog/rate-presentation-dialog.directive';
import {AuthenticatedDirective} from './authenticated.directive';
import {CommentsComponent} from './presentation/comments/comments.component';
import {Ng2GoogleChartsModule} from 'ng2-google-charts';


@NgModule({
  imports: [
    Ng2GoogleChartsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSliderModule,
    CommonModule,
    FileUploadModule,
    FormsModule,
    MatIconModule,
    MatButtonModule,
    MatChipsModule,
    MatMenuModule,
    MatCardModule,
    MatExpansionModule,
    MatTooltipModule,
    MatSnackBarModule,
    MatProgressSpinnerModule
  ],
  declarations: [
    AuthenticatedDirective,
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
    RatePresentationComponent,
    LikeButtonComponent,
    CommentsComponent,
    PresentationMetadataComponent,
    RatePresentationDialogComponent,
    RatePresentationDialogDirective,
    RateSliderComponent
  ],
  exports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    AuthenticatedDirective,
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
    CommentsComponent,
    MatTooltipModule,
    PresentationMetadataComponent,
    RatePresentationDialogDirective,
    RatePresentationComponent
  ],
  providers: [PersonModalService, LikeService],
  entryComponents: [RatePresentationDialogComponent]
})
export class SharedModule {

}
