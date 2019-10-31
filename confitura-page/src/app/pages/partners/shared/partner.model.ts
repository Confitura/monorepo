export class Partner {
  static readonly TYPES = ['platinum', 'gold', 'big data & AI', 'silver', 'bronze', 'media', 'technical'];
  id: string;
  name: string;
  description: string;
  logo: string;
  www: string;
  type: string;
  published: boolean;
}
