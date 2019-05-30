import {User} from '../../core/user/user.model';

export class Presentation {
  id: string;
  title: string;
  language: string;
  level: string;
  speakers: User[] = [];
  tags: string;
  shortDescription: string;
  description: string;
  status: string;
}

export enum DescriptionType {
  Both = 'both',
  Short = 'short',
  Full = 'full'
}
