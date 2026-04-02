package com.runnect.runnect.presentation.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runnect.runnect.R
import com.runnect.runnect.presentation.ui.theme.G2
import com.runnect.runnect.presentation.ui.theme.M1
import com.runnect.runnect.presentation.ui.theme.PretendardFontFamily
import com.runnect.runnect.presentation.ui.theme.White

@Composable
fun VisitorModeScreen(
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.finish_run),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(13.dp))
        Text(
            text = stringResource(R.string.visitor_mode_mypage_message),
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = G2,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(22.dp))
        Button(
            onClick = onSignUpClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 73.dp)
                .height(40.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = M1)
        ) {
            Text(
                text = stringResource(R.string.visitor_mode_signup_btn),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = White
            )
        }
    }
}
