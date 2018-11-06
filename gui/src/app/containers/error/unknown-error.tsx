import * as React from 'react';
import { Component, ReactNode } from 'react';
import { Button, Container, Message, Segment } from 'semantic-ui-react'

class UnknownErrorContainer extends Component {
    public render(): ReactNode {
        return (
            <Container className="error error-unknown">
                <Segment basic>
                    <Message
                        negative
                        icon='frown outline'
                        header='Unknown error'
                        content='An unknown error occurred' />
                    <a href='/'>
                        <Button primary icon='home' size='mini' content='Home' />
                    </a>
                </Segment>
            </Container>
        );
    }
}

export { UnknownErrorContainer };