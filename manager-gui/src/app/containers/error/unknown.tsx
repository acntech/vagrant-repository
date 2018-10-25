import * as React from 'react';
import { Component, ReactNode } from 'react';
import { Link } from 'react-router-dom';
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
                    <Link to='/'>
                        <Button primary icon='home' size='mini' content='Home' />
                    </Link>
                </Segment>
            </Container>
        );
    }
}

export { UnknownErrorContainer };