import * as React from 'react';
import { Component, ReactNode } from 'react';
import { Container, Segment } from 'semantic-ui-react'

import { PrimaryHeader } from '../';

class LoadingIndicatorComponent extends Component<{}> {

    public render(): ReactNode {
        return (
            <Container>
                <PrimaryHeader />
                <Segment loading>
                    <div className='loading-indicator-body' />
                </Segment>
            </Container>
        );
    }
}

export { LoadingIndicatorComponent as LoadingIndicator };