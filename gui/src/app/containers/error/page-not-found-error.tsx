import * as React from 'react';
import { Component, ReactNode } from 'react';
import { Button } from 'semantic-ui-react';

import { NotFoundErrorContainer } from './not-found-error';

class PageNotFoundErrorContainer extends Component {

    public render(): ReactNode {
        return (
            <NotFoundErrorContainer
                icon='blind'
                heading='Page not found'
                content='Can not find the page you are looking for'>
                <a href='/'>
                    <Button primary icon='home' size='mini' content='Home' />
                </a>
            </NotFoundErrorContainer>
        );
    }
}

export { PageNotFoundErrorContainer };