describe('App', function () {

  beforeEach(function () {
    browser.get('/');
  });

  it('should have a title', function () {
    expect(browser.getTitle()).toEqual("TickTag");
  });

  it('should have <header>', function () {
    expect(element(by.css('tt-app header')).isPresent()).toEqual(true);
  });

  it('should have <main>', function () {
    expect(element(by.css('tt-app main')).isPresent()).toEqual(true);
  });

  it('should have a main title', function () {
    expect(element(by.css('tt-app h1')).getText()).toEqual('Hello from TickTag!');
  });

  it('should have <footer>', function () {
    expect(element(by.css('tt-app footer')).isPresent()).toEqual(true);
  });
});
