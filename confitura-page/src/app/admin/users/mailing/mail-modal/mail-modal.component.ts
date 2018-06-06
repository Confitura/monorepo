import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {User} from '../../../../core/user/user.model';
import {MailService, MessageInfo} from '../mail.service';

@Component({
    selector: 'cf-mail-modal',
    templateUrl: './mail-modal.component.html',
    styleUrls: ['./mail-modal.component.css']
})
export class MailModalComponent {
    users: User[] = [];
    templates: string[] = [];
    template: string;

    constructor(
        public dialogRef: MatDialogRef<MailModalComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private mailService: MailService) {
        this.users = data.users;
        this.mailService.getTemplates().subscribe(
            it => this.templates = it
        );
    }


    onNoClick(): void {
        this.dialogRef.close();
    }

    send() {
        if (this.template) {
            const info = this.users.map(it => this.asMail(it));
            this.mailService.sendMail(this.template, info)
                .subscribe(it => this.dialogRef.close());

        }
    }

    private asMail(it: User): MessageInfo {
        return {email: it.email};
    }
}
