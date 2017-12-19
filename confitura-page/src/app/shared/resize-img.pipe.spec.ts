import {ResizeImgPipe} from './resize-img.pipe';
import {environment} from '../../environments/environment';

describe('ResizeImgPipe', () => {

  describe('in production environment', () => {

    beforeEach(() => {
      environment.production = true;
    });

    it('should resize with default width', () => {
      const pipe = new ResizeImgPipe();
      expect(pipe.transform('/api/resources/photos/1.jpg'))
        .toEqual('/api/resources/photos/350/1.jpg');
    });

    it('should resize with given width', () => {
      const pipe = new ResizeImgPipe();
      expect(pipe.transform('/api/resources/photos/2.jpg', 100))
        .toEqual('/api/resources/photos/100/2.jpg');
    });

    it('should not resize if path does not contain /photos', () => {
      const pipe = new ResizeImgPipe();
      expect(pipe.transform('/api/resources/3.jpg', 100))
        .toEqual('/api/resources/3.jpg');
    });

    it('should do nothing if null', () => {
      const pipe = new ResizeImgPipe();
      expect(pipe.transform(null, 100))
        .toBeNull();
    });

  });

  it('should not resize in development mode', () => {
    environment.production = false;
    const pipe = new ResizeImgPipe();
    expect(pipe.transform('/api/resources/photos/4.jpg'))
      .toEqual('/api/resources/photos/4.jpg');
  });

});
